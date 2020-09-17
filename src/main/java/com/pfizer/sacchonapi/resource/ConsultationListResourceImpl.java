package com.pfizer.sacchonapi.resource;

import com.pfizer.sacchonapi.exception.BadEntityException;
import com.pfizer.sacchonapi.exception.NotFoundException;
import com.pfizer.sacchonapi.model.Consultation;
import com.pfizer.sacchonapi.repository.ConsultationRepository;
import com.pfizer.sacchonapi.repository.util.JpaUtil;
import com.pfizer.sacchonapi.representation.ConsultationRepresentation;
import com.pfizer.sacchonapi.resource.util.ResourceValidator;
import com.pfizer.sacchonapi.security.ResourceUtils;
import com.pfizer.sacchonapi.security.Shield;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsultationListResourceImpl extends ServerResource implements ConsultationListResource {

    public static final Logger LOGGER = Engine.getLogger(ConsultationResource.class);

    private ConsultationRepository consultationRepository;

    @Override
    protected void doInit() {
        LOGGER.info("Initialising consultation resource starts");
        try {
            consultationRepository =
                    new ConsultationRepository(JpaUtil.getEntityManager());
        } catch (Exception e) {

        }
        LOGGER.info("Initialising consultation resource ends");
    }

    public List<ConsultationRepresentation> getConsultations() throws NotFoundException {

        LOGGER.finer("Select all consultations.");

        // Check authorization
        ResourceUtils.checkRoles(this, Shield.ROLE_PATIENT, Shield.ROLE_DOCTOR);


        try {
            List<Consultation> consultations =
                    consultationRepository.findAll();
            List<ConsultationRepresentation> result =
                    new ArrayList<>();

            consultations.forEach(consultation ->
                    result.add(new ConsultationRepresentation(consultation)));

            return result;
        } catch (Exception e) {
            throw new NotFoundException("consultations not found");
        }
    }

    @Override
    public ConsultationRepresentation add
            (ConsultationRepresentation consultationRepresentationIn)
            throws BadEntityException {

        LOGGER.finer("Add a new consultation.");

        // Check authorization
        ResourceUtils.checkRole(this, Shield.ROLE_PATIENT);
        LOGGER.finer("User allowed to add a consultation.");

        // Check entity

        ResourceValidator.notNull(consultationRepresentationIn);
        ResourceValidator.validate(consultationRepresentationIn);
        LOGGER.finer("Consultation checked");

        try {
            // Convert CompanyRepresentation to Company
            Consultation consultationIn = new Consultation();
            consultationIn.setMedicationName(consultationRepresentationIn.getMedicationName());
            consultationIn.setDosage(consultationRepresentationIn.getDosage());
            consultationIn.setConsultationDate(
                    consultationRepresentationIn.getConsultationDate());


            Optional<Consultation> consultationOut =
                    consultationRepository.save(consultationIn);
            Consultation consultation = null;
            if (consultationOut.isPresent())
                consultation = consultationOut.get();
            else
                throw new
                        BadEntityException(" Consultation has not been created");

            ConsultationRepresentation result =
                    new ConsultationRepresentation();
            result.setConsultationDate(consultation.getConsultationDate());
            result.setDosage(consultation.getDosage());
            result.setMedicationName(consultation.getMedicationName());
            result.setUri("http://localhost:9000/v1/consultation/"+consultation.getId());

            getResponse().setLocationRef(
                    "http://localhost:9000/v1/consultation/"+consultation.getId());
            getResponse().setStatus(Status.SUCCESS_CREATED);

            LOGGER.finer("Consultation successfully added.");

            return result;
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Error when adding a consultation", ex);

            throw new ResourceException(ex);
        }
    }
}