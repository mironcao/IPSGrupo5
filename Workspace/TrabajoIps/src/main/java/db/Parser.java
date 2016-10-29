package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import logic.Actividad;
import logic.Instalacion;
import logic.Recibo;
import logic.Reserva;
import logic.Socio;

public class Parser {
	
	ArrayList<Socio> socios = new ArrayList<>();
	ArrayList<Instalacion> instalaciones = new ArrayList<>();
	ArrayList<Reserva> reservas = new ArrayList<>();
	ArrayList<Recibo> recibos = new ArrayList<>();
	ArrayList<Actividad> actividades = new ArrayList<>();
	public ArrayList<Socio> getSocios() {
		return socios;
	}

	public ArrayList<Instalacion> getInstalaciones() {
		return instalaciones;
	}

	public ArrayList<Reserva> getReservas() {
		return reservas;
	}

	public ArrayList<Recibo> getRecibos() {
		return recibos;
	}

	public ArrayList<Actividad> getActividades() {
		return actividades;
	}

	Connection c = Database.getInstance().getC();
	
	public void fillArrays() throws SQLException
	{
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("select * from SOCIO");
		while(rs.next())
		{
			socios.add(new Socio(rs.getString("socioID")));
		}
		
		s = c.createStatement();
		rs = s.executeQuery("select * from INSTALACION");
		while(rs.next())
		{
			instalaciones.add(new Instalacion(rs.getString("instalacionID")));
		}
		
		s = c.createStatement();
		rs = s.executeQuery("Select * From RESERVA");
		while(rs.next())
		{
			reservas.add(new Reserva(rs.getInt("reservaID"), rs.getInt("socioID"), rs.getInt("instalacionID"),
					rs.getTimestamp("horaComienzo"), rs.getTimestamp("horaFinal"),
					rs.getTimestamp("horaEntrada"), rs.getTimestamp("horaSalida"), 
					rs.getString("modoPago"), rs.getInt("precio")));
		}
		
		// TODO cuota y entrada_cuota
		
//		s = c.createStatement();
//		rs = s.executeQuery("Select * From ACTIVIDAD");
//		while(rs.next())
//		{
//			actividades.add(new Actividad(rs.getString("actividadID"), rs.getTimestamp("fechaComienzo"), rs.getTimestamp("fechaFinal")));
//		}
//		
	}
	
	/**
	 * Comprueba si est� ocupada en una franja horaria
	 * 
	 * @param horaC,
	 *            hora de comienzo
	 * @param horaF,
	 *            hora final
	 * @return true si est� disponible, false si no
	 */
	public boolean comprobarDisponibilidadPorInstalacion(int instalacionID, Date horaC, Date horaF) {
		boolean resultado = true;

		for (Reserva reserva : reservas) {
			if (reserva.getHoraComienzo().getHours()==horaC.getHours() && reserva.getHoraFinal().getHours()==horaF.getHours() && reserva.getInstalacionID()==instalacionID) {
				resultado = false;
			}
		}
		return resultado;
	}
	
	
	/**
	 * Comprueba si un socio tiene mas de una instalacion reservada simultaneamente
	 * 
	 * @param horaC,
	 *            hora de comienzo
	 * @param horaF,
	 *            hora final
	 * @return true si la tiene, false si no la tiene
	 */
	public boolean comprobarDisponibilidadPorSocio(int socioID, Date horaC, Date horaF) {
		boolean resultado = false;

		for (Reserva reserva : reservas) {
			if (reserva.getHoraComienzo().getHours()==horaC.getHours() && reserva.getHoraFinal().getHours()==horaF.getHours() && reserva.getSocioID()==socioID) {
				resultado = true;
			}
		}
		return resultado;
	}
}
