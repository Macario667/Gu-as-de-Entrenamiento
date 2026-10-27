package com.inklusport.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped 
public class ConexionService {
    
    @Inject 
    DataSource dataSource;

    public Map<String, Object> verificarConexion() throws SQLException{
        String sql = "SELECT DATABASE(), 1";

        try (Connection conexion = dataSource.getConnection();
             PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            resultado.next();

            return Map.of(
                "estado", "conectado",
                "baseDatos", resultado.getString(1),
                "resultado", resultado.getInt(2)
            );
        }
    }
}
