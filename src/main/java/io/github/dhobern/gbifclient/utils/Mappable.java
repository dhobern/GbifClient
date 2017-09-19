/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.gbifclient.utils;

import java.time.LocalDate;

/**
 *
 * @author Platyptilia
 */
public interface Mappable {
    public Double getDecimalLatitude();
    public Double getDecimalLongitude();
    public String getCountryCode();
    public LocalDate getDate();
    public String getNamedCategory(String category);
    public int getCount();
}
