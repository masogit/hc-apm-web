package com.ge.apm.domain.acquisition;

import java.util.List;

public  class MedicalInstitution {

    private String name;

    private String uid;

    private List<String> uids;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<String> getUids() {
            return uids;
        }

        public void setUids(List<String> uids) {
            this.uids = uids;
        }
    }
