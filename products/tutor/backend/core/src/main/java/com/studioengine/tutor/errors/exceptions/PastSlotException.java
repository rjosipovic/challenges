package com.studioengine.tutor.errors.exceptions;

import com.studioengine.tutor.errors.ErrorCode;
import com.studioengine.tutor.errors.TutorEngineException;

/** Thrown when tutor attempts to publish or create a time slot that is in the past */
public class PastSlotException extends TutorEngineException {

    public PastSlotException(String detail) {
        super(ErrorCode.SLOT_IN_PAST, detail);
    }
}
