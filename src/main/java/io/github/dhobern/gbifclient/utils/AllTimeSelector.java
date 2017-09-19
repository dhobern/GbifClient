/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

/**
 *
 * @author Platyptilia
 */
public class AllTimeSelector implements CategorySelector {
    
    public AllTimeSelector() {
    }

    public int getCategoryCount() {
        return 1;
    }

    public int getCategory(Mappable bin) {
        return 0;
    }

    public String getCategoryLabel(int index) {
        return "ALLTIME";
    }
    
    public String getName() {
        return "timePeriod";
    }
}