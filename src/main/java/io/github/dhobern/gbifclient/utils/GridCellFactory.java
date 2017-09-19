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
public class GridCellFactory extends CellValueFactory {
    
    public CellValue createCellValue(int[] position, Mappable m) {
        return new GridCell(position, m);
    }

}
