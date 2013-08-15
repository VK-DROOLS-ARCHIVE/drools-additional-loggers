package org.drools.test;

import org.drools.scorecards.DroolsScorecard;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.logger.KnowledgeRuntimeLogger;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.loggers.html5.HTML5FileLogger;

import java.io.InputStream;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

public class DrlTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testDRLExecution() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        InputStream stream = DrlTest.class.getResourceAsStream("/scoremodel.drl");
        kbuilder.add( ResourceFactory.newInputStreamResource(stream), ResourceType.DRL);
        for (KnowledgeBuilderError error : kbuilder.getErrors()){
            System.out.println(error.getMessage());
        }
        assertFalse( kbuilder.hasErrors() );

        //BUILD RULEBASE
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        KnowledgeRuntimeLogger logger = HTML5FileLogger.create(session, "d:\\Temp\\scorecard.html", 1);

        FactType scorecardType = kbase.getFactType( "org.drools.scorecards.example","SampleScore" );
        DroolsScorecard scorecard = (DroolsScorecard) scorecardType.newInstance();
        scorecardType.set(scorecard, "residenceState", "AP");
        scorecardType.set(scorecard, "occupation", "TEACHER");
        scorecardType.set(scorecard, "age", 20);
        scorecardType.set(scorecard, "validLicense", true);
        session.insert( scorecard );
        session.fireAllRules();
        session.dispose();
        logger.close();

        //occupation = +10, age = +40, state = -10, validLicense = 1
        assertEquals(41.0, scorecard.getCalculatedScore());
    }


}
