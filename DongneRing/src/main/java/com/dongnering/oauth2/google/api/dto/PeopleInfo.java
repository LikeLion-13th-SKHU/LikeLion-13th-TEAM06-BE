package com.dongnering.oauth2.google.api.dto;

import java.util.List;

public record PeopleInfo(
        List<PeopleName> names,
        List<PeopleBirthday> birthdays
) { }