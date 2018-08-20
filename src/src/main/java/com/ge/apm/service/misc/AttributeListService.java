package com.ge.apm.service.misc;

import com.ge.apm.dao.AttrDescRepository;
import com.ge.apm.dao.AttributeRepository;
import com.ge.apm.domain.AttrDesc;
import com.ge.apm.domain.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;

import java.util.List;
import java.util.Optional;

@Service
public class AttributeListService {

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private AttrDescRepository attrDescRepository;

    public Page<Attribute> findAllAttribute(PageRequest pageRequest) {
        return attributeRepository.findAll(pageRequest);
    }

    public Page<Attribute> findAttrsBySearchFilters(List<SearchFilter> searchFilters, PageRequest pageRequest) {
        return attributeRepository.findBySearchFilter(searchFilters, pageRequest);
    }

    public List<AttrDesc> findAttrDescByAttributeId(String attributeId) {
        List<AttrDesc> attrDescList = attrDescRepository.findByAttributeId(attributeId);
        return attrDescList;
    }

    public Page<AttrDesc> findAttrDescsBySearchFilters(List<SearchFilter> searchFilters, PageRequest pageRequest) {
        return attrDescRepository.findBySearchFilter(searchFilters, pageRequest);
    }

    public Boolean isAttributeExisted(Attribute attribute) {
        boolean isExisted = false;
        List<Attribute> attributes = attributeRepository.findByName(attribute.getName());
        if(attributes != null && attributes.size() > 0) {
            isExisted = true;
        }
        return isExisted;
    }

    public Boolean isAttrDescExistByName(String name) {
        boolean isExist = false;
        List<AttrDesc> attrDescs = attrDescRepository.findByName(name);
        if(attrDescs != null && attrDescs.size() > 0) {
            isExist = true;
        }
        return isExist;
    }


    public void deleteAttrDescByAttributeId(String attributeId) {
        this.attrDescRepository.deleteAttrDescByAttributeId(attributeId);
    }

    public AttributeRepository getAttributeRepository() {
        return this.attributeRepository;
    }

    public AttrDescRepository getAttrDescRepository() {
        return this.attrDescRepository;
    }
}
