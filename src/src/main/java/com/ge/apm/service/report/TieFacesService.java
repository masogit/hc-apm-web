/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.report;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.tiefaces.components.websheet.dataobjects.TieCommandAlias;
/**
 *
 * @author 212579464
 */
@Component
public class TieFacesService {
    
    public static List<TieCommandAlias> commonAliasList() {

        List<TieCommandAlias> list = new ArrayList<>();

        String commentForDate = "$widget.calendar{showOn=\"button\" pattern=\"yyyy/MM/dd\" readonlyInput=\"true\"}";
        String commentForFloat = "$widget.inputnumber{decimalPlaces=\"2\"}";
        String commentForInteger = "$widget.inputnumber{decimalPlaces=\"0\"}";
        String commentForSwitch = "$widget.dropdown{itemLabels=\"是;否\" itemValues=\"是;否\" }";

        list.add(new TieCommandAlias("${*.D*}", commentForDate));
        list.add(new TieCommandAlias("${*.F*}", commentForFloat));
        list.add(new TieCommandAlias("${*.S*}", commentForSwitch));
        list.add(new TieCommandAlias("${*.I*}", commentForInteger));

        return list;
    }
}
