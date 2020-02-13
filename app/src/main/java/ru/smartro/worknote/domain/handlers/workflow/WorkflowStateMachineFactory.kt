package ru.smartro.worknote.domain.handlers.workflow

import ru.smartro.worknote.domain.handlers.workflow.events.Event
import ru.smartro.worknote.domain.handlers.workflow.sideEffects.SideEffect
import ru.smartro.worknote.domain.handlers.workflow.states.State
import ru.smartro.worknote.utils.stateMachine.StateMachine

class WorkflowStateMachineFactory {

    fun build(): StateMachine<State, Event, SideEffect> {
        return StateMachine.create {
            initialState(State.Created)
        }
    }
}

