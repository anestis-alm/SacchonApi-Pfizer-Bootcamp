package com.pfizer.sacchonapi.security;

import org.restlet.Application;
import org.restlet.data.ChallengeScheme;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.MemoryRealm;
import org.restlet.security.User;
import org.restlet.security.Verifier;


public class Shield {
    public static final String ROLE_CHIEF_DOCTOR = "chiefDoctor";
    public static final String ROLE_DOCTOR = "doctor";
    public static final String ROLE_PATIENT = "patient";


    private Application application;

    public Shield(Application application) {
        this.application = application;
    }


    public ChallengeAuthenticator createApiGuard() {

        ChallengeAuthenticator apiGuard = new ChallengeAuthenticator(
                application.getContext(), ChallengeScheme.HTTP_BASIC, "realm");


        // - Verifier : checks authentication
        // - Enroler : to check authorization (roles)
        Verifier verifier = new CustomVerifier();
        apiGuard.setVerifier(verifier);

        return apiGuard;
    }





    /**
     * not used
     * it is given as reference for hard-coded definition of users
     * @return
     */
    public ChallengeAuthenticator createApiGuard1() {

        ChallengeAuthenticator apiGuard = new ChallengeAuthenticator(
                application.getContext(), ChallengeScheme.HTTP_BASIC, "realm");

        // Create in-memory users and roles.
        MemoryRealm realm = new MemoryRealm();

        User doctor = new User("doctor", "doctor");
        realm.getUsers().add(doctor);
        realm.map(doctor, application.getRole(ROLE_DOCTOR));
        realm.map(doctor, application.getRole(ROLE_PATIENT));

        User chiefDoctor = new User("chiefDoctor", "chiefDoctor");
        realm.getUsers().add(chiefDoctor);
        realm.map(chiefDoctor, application.getRole(ROLE_CHIEF_DOCTOR));
        realm.map(chiefDoctor, application.getRole(ROLE_CHIEF_DOCTOR));
        realm.map(chiefDoctor, application.getRole(ROLE_CHIEF_DOCTOR));

        User patient = new User("patient", "patient");
        realm.getUsers().add(patient);
        realm.map(patient, application.getRole(ROLE_PATIENT));

        // - Verifier : checks authentication
        // - Enroler : to check authorization (roles)
        apiGuard.setVerifier(realm.getVerifier());
        apiGuard.setEnroler(realm.getEnroler());

        // Provide your own authentication checks by extending SecretVerifier or
        // LocalVerifier classes
        // Extend the Enroler class in order to assign roles for an
        // authenticated user

        return apiGuard;
    }



}
