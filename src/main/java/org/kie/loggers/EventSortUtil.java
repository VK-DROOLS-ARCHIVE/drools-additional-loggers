package org.kie.loggers;

import org.drools.core.audit.event.*;

import java.util.*;

public class EventSortUtil {

    public static List<Event> createEventList(List<LogEvent> logEvents) {
        Iterator<LogEvent> iterator = logEvents.iterator();
        List<Event> events = new ArrayList<Event>();
        Stack<Event> beforeEvents = new Stack<Event>();
        List<Event> newActivations = new ArrayList<Event>();
        Map<String, Event> activationMap = new HashMap<String, Event>();
        Map<Long, Event> objectMap = new HashMap<Long, Event>();
        while (iterator.hasNext()) {
            LogEvent inEvent = iterator.next();
            Event event = new Event(inEvent.getType());
            switch (inEvent.getType()) {
                case LogEvent.INSERTED:
                    ObjectLogEvent inObjectEvent = (ObjectLogEvent) inEvent;
                    event.setString("Object inserted (" + inObjectEvent.getFactId() + "): " + inObjectEvent.getObjectToString());
                    if (!beforeEvents.isEmpty()) {
                        beforeEvents.peek().addSubEvent(event);
                    } else {
                        events.add(event);
                    }
                    event.addSubEvents(newActivations);
                    newActivations.clear();
                    objectMap.put(((ObjectLogEvent) inEvent).getFactId(), event);
                    break;
                case LogEvent.UPDATED:
                    inObjectEvent = (ObjectLogEvent) inEvent;
                    event.setString("Object updated (" + inObjectEvent.getFactId() + "): " + inObjectEvent.getObjectToString());
                    if (!beforeEvents.isEmpty()) {
                        beforeEvents.peek().addSubEvent(event);
                    } else {
                        events.add(event);
                    }
                    event.addSubEvents(newActivations);
                    newActivations.clear();
                    Event assertEvent = objectMap.get(((ObjectLogEvent) inEvent).getFactId());
                    if (assertEvent != null) {
                        event.setCauseEvent(assertEvent);
                    }
                    break;
                case LogEvent.RETRACTED:
                    inObjectEvent = (ObjectLogEvent) inEvent;
                    event.setString("Object removed (" + inObjectEvent.getFactId() + "): " + inObjectEvent.getObjectToString());
                    if (!beforeEvents.isEmpty()) {
                        beforeEvents.peek().addSubEvent(event);
                    } else {
                        events.add(event);
                    }
                    event.addSubEvents(newActivations);
                    newActivations.clear();
                    assertEvent = objectMap.get(((ObjectLogEvent) inEvent).getFactId());
                    if (assertEvent != null) {
                        event.setCauseEvent(assertEvent);
                    }
                    break;
                case LogEvent.ACTIVATION_CREATED:
                    ActivationLogEvent inActivationEvent = (ActivationLogEvent) inEvent;
                    event.setString("Activation created: Rule " + inActivationEvent.getRule() + " " + inActivationEvent.getDeclarations());
                    newActivations.add(event);
                    activationMap.put(((ActivationLogEvent) inEvent).getActivationId(), event);
                    break;
                case LogEvent.ACTIVATION_CANCELLED:
                    inActivationEvent = (ActivationLogEvent) inEvent;
                    event.setString("Activation cancelled: Rule " + inActivationEvent.getRule() + " " + inActivationEvent.getDeclarations());
                    newActivations.add(event);
                    event.setCauseEvent(activationMap.get(((ActivationLogEvent) inEvent).getActivationId()));
                    break;
                case LogEvent.BEFORE_ACTIVATION_FIRE:
                    inActivationEvent = (ActivationLogEvent) inEvent;
                    event.setString("Activation executed: Rule " + inActivationEvent.getRule() + " " + inActivationEvent.getDeclarations());
                    events.add(event);
                    beforeEvents.push(event);
                    event.setCauseEvent(activationMap.get(((ActivationLogEvent) inEvent).getActivationId()));
                    break;
                case LogEvent.AFTER_ACTIVATION_FIRE:
                    beforeEvents.pop();
                    break;
                case LogEvent.BEFORE_RULEFLOW_CREATED:
                    RuleFlowLogEvent inRuleFlowEvent = (RuleFlowLogEvent) inEvent;
                    event.setString("Process started: " + inRuleFlowEvent.getProcessName() + "[" + inRuleFlowEvent.getProcessId() + "]");
                    if (!beforeEvents.isEmpty()) {
                        beforeEvents.peek().addSubEvent(event);
                    } else {
                        events.add(event);
                    }
                    beforeEvents.push(event);
                    break;
                case LogEvent.AFTER_RULEFLOW_CREATED:
                    beforeEvents.pop();
                    break;
                case LogEvent.BEFORE_RULEFLOW_COMPLETED:
                    inRuleFlowEvent = (RuleFlowLogEvent) inEvent;
                    event.setString("Process completed: " + inRuleFlowEvent.getProcessName() + "[" + inRuleFlowEvent.getProcessId() + "]");
                    if (!beforeEvents.isEmpty()) {
                        beforeEvents.peek().addSubEvent(event);
                    } else {
                        events.add(event);
                    }
                    beforeEvents.push(event);
                    break;
                case LogEvent.AFTER_RULEFLOW_COMPLETED:
                    beforeEvents.pop();
                    break;
                case LogEvent.BEFORE_RULEFLOW_NODE_TRIGGERED:
                    RuleFlowNodeLogEvent inRuleFlowNodeEvent = (RuleFlowNodeLogEvent) inEvent;
                    event.setString("Process node triggered: " + inRuleFlowNodeEvent.getNodeName() + " in process " + inRuleFlowNodeEvent.getProcessName() + "[" + inRuleFlowNodeEvent.getProcessId() + "]");
                    if (!beforeEvents.isEmpty()) {
                        beforeEvents.peek().addSubEvent(event);
                    } else {
                        events.add(event);
                    }
                    beforeEvents.push(event);
                    break;
                case LogEvent.AFTER_RULEFLOW_NODE_TRIGGERED:
                    beforeEvents.pop();
                    break;
                case LogEvent.BEFORE_RULEFLOW_GROUP_ACTIVATED:
                    RuleFlowGroupLogEvent inRuleFlowGroupEvent = (RuleFlowGroupLogEvent) inEvent;
                    event.setString("RuleFlow Group activated: " + inRuleFlowGroupEvent.getGroupName() + "[size=" + inRuleFlowGroupEvent.getSize() + "]");
                    if (!beforeEvents.isEmpty()) {
                        beforeEvents.peek().addSubEvent(event);
                    } else {
                        events.add(event);
                    }
                    beforeEvents.push(event);
                    break;
                case LogEvent.AFTER_RULEFLOW_GROUP_ACTIVATED:
                    beforeEvents.pop();
                    break;
                case LogEvent.BEFORE_RULEFLOW_GROUP_DEACTIVATED:
                    inRuleFlowGroupEvent = (RuleFlowGroupLogEvent) inEvent;
                    event.setString("RuleFlow Group deactivated: " + inRuleFlowGroupEvent.getGroupName() + "[size=" + inRuleFlowGroupEvent.getSize() + "]");
                    if (!beforeEvents.isEmpty()) {
                        beforeEvents.peek().addSubEvent(event);
                    } else {
                        events.add(event);
                    }
                    beforeEvents.push(event);
                    break;
                case LogEvent.AFTER_RULEFLOW_GROUP_DEACTIVATED:
                    beforeEvents.pop();
                    break;
                case LogEvent.BEFORE_PACKAGE_ADDED:
                    RuleBaseLogEvent ruleBaseEvent = (RuleBaseLogEvent) inEvent;
                    event.setString("Package added: " + ruleBaseEvent.getPackageName());
                    if (!beforeEvents.isEmpty()) {
                        beforeEvents.peek().addSubEvent(event);
                    } else {
                        events.add(event);
                    }
                    beforeEvents.push(event);
                    break;
                case LogEvent.AFTER_PACKAGE_ADDED:
                    beforeEvents.pop();
                    break;
                case LogEvent.BEFORE_PACKAGE_REMOVED:
                    ruleBaseEvent = (RuleBaseLogEvent) inEvent;
                    event.setString("Package removed: " + ruleBaseEvent.getPackageName());
                    if (!beforeEvents.isEmpty()) {
                        beforeEvents.peek().addSubEvent(event);
                    } else {
                        events.add(event);
                    }
                    beforeEvents.push(event);
                    break;
                case LogEvent.AFTER_PACKAGE_REMOVED:
                    beforeEvents.pop();
                    break;
                case LogEvent.BEFORE_RULE_ADDED:
                    ruleBaseEvent = (RuleBaseLogEvent) inEvent;
                    event.setString("Rule added: " + ruleBaseEvent.getRuleName());
                    if (!beforeEvents.isEmpty()) {
                        beforeEvents.peek().addSubEvent(event);
                    } else {
                        events.add(event);
                    }
                    beforeEvents.push(event);
                    break;
                case LogEvent.AFTER_RULE_ADDED:
                    if (!beforeEvents.isEmpty()) {
                        Event beforeEvent = beforeEvents.pop();
                        beforeEvent.addSubEvents(newActivations);
                        newActivations.clear();
                    }
                    break;
                case LogEvent.BEFORE_RULE_REMOVED:
                    ruleBaseEvent = (RuleBaseLogEvent) inEvent;
                    event.setString("Rule removed: " + ruleBaseEvent.getRuleName());
                    if (!beforeEvents.isEmpty()) {
                        beforeEvents.peek().addSubEvent(event);
                    } else {
                        events.add(event);
                    }
                    beforeEvents.push(event);
                    break;
                case LogEvent.AFTER_RULE_REMOVED:
                    if (!beforeEvents.isEmpty()) {
                        Event beforeEvent = beforeEvents.pop();
                        beforeEvent.addSubEvents(newActivations);
                        newActivations.clear();
                    }
                    break;
                default:
                    // do nothing
                    break;
            }
        }
        return events;
    }
}
