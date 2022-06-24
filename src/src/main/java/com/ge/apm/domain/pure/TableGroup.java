/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain.pure;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 212579464
 */
@Data
public class TableGroup {
    private String title;
    private List<TableItem> items =new ArrayList();;

}