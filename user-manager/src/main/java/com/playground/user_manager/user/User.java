package com.playground.user_manager.user;

import lombok.*;

@Value
@Getter
@ToString
@EqualsAndHashCode
public class User {

    String id;
    String alias;
}
