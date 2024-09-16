package org.example;

public class AddExperienceRequest implements ExperienceStrategy{
    @Override
    public int calculateExperience() {
        return 10;
    }
}
