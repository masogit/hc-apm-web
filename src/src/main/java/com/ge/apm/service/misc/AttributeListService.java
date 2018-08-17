package com.ge.apm.service.misc;

import com.ge.apm.dao.AttrDescRepository;
import com.ge.apm.dao.AttributeRepository;
import com.ge.apm.domain.AttrDesc;
import com.ge.apm.domain.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttributeListService {

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private AttrDescRepository attrDescRepository;

    public Page<Attribute> findAllAttribute(PageRequest pageRequest) {
        return attributeRepository.findAll(pageRequest);
    }

    public List<AttrDesc> findAttrDescByAttributeId(String attributeId) {
        List<AttrDesc> attrDescList = attrDescRepository.findByAttributeId(attributeId);
        return attrDescList;
    }

    public Boolean isAttributeExistByName(String name) {
        boolean isExist = false;
        List<Attribute> attributes = attributeRepository.findByName(name);
        if(attributes != null && attributes.size() > 0) {
            isExist = true;
        }
        return isExist;
    }

    public Boolean isAttrDescExistByName(String name) {
        boolean isExist = false;
        List<AttrDesc> attrDescs = attrDescRepository.findByName(name);
        if(attrDescs != null && attrDescs.size() > 0) {
            isExist = true;
        }
        return isExist;
    }

    public AttributeRepository getAttributeRepository() {
        return this.attributeRepository;
    }

    public AttrDescRepository getAttrDescRepository() {
        return this.attrDescRepository;
    }
}
