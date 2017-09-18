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

    @Override
    public int getCategoryCount() {
        return 1;
    }

    @Override
    public int getCategory(OccurrenceBin bin) {
        return 0;
    }

    @Override
    public String getCategoryLabel(int index) {
        return "ALLTIME";
    }
}