package com.ge.apm.service.misc;

import com.ge.apm.dao.AttrDescRepository;
import com.ge.apm.dao.AttrValueRepository;
import com.ge.apm.dao.AttributeRepository;
import com.ge.apm.domain.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AttributeListService {

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private AttrDescRepository attrDescRepository;

    @Autowired
    private AttrValueRepository attrValueRepository;

    public Page<Attribute> findAll(PageRequest pageRequest) {
        return attributeRepository.findAll(pageRequest);
    }

}
