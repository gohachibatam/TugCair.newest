package com.gohachi.tugcair;

import androidx.annotation.NonNull;

public class PersonId {

    public String personId;

    public <T extends PersonId>T withId(@NonNull final String id){
        this.personId = id;
        return (T) this;
    }
}
