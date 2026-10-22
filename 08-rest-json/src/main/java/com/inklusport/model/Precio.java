package com.inklusport.model;

import java.util.Objects;
import java.util.Locale;

/**
 * Objeto de valor con formato propio: se serializa como texto
 * "12.50 EUR" mediante un serializador personalizado.
 */
public class Precio {

    private final double importe;
    private final String moneda;

    public Precio(double importe, String moneda) {
        this.importe = importe;
        this.moneda = moneda;
    }

    public double getImporte() {
        return importe;
    }

    public String getMoneda() {
        return moneda;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Precio otro)) {
            return false;
        }
        return Double.compare(importe, otro.importe) == 0 && Objects.equals(moneda, otro.moneda);
    }

    @Override
    public int hashCode() {
        return Objects.hash(importe, moneda);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%.2f %s", importe, moneda);
    }
}
