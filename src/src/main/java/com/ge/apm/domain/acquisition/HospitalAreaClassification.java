package com.ge.apm.domain.acquisition;

import java.util.ArrayList;
import java.util.List;

public class HospitalAreaClassification {
    /**
     *  医共体
     */
   private List<MedicalInstitution> medicalConsortium = new ArrayList<>();

    /**
     *   单体医院
     */
   private List<MedicalInstitution> standaloneHospitals = new ArrayList<>();

    public List<MedicalInstitution> getMedicalConsortium() {
        return medicalConsortium;
    }

    public void setMedicalConsortium(List<MedicalInstitution> medicalConsortium) {
        this.medicalConsortium = medicalConsortium;
    }

    public List<MedicalInstitution> getStandaloneHospitals() {
        return standaloneHospitals;
    }

    public void setStandaloneHospitals(List<MedicalInstitution> standaloneHospitals) {
        this.standaloneHospitals = standaloneHospitals;
    }

}
