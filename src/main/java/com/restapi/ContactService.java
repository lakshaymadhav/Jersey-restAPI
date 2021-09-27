package com.restapi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ContactService {

	Connection con=ConnectionProvider.getCon();
	
	public  List<Contact> getAllContacts(){
		List<Contact> list=new ArrayList<Contact>();
		try {
			Statement st=con.createStatement();
			ResultSet rs=st.executeQuery("select * from contactlist");
			while(rs.next()) {
				Contact contact=new Contact();
				contact.setName(rs.getString(2));
				contact.setNumber(rs.getString(3));
				contact.setEmail(rs.getString(4));
				list.add(contact);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public Map<String, String> addoneContact(Contact a) {
		Map<String, String> map=new HashMap();
		try {
			int check=0;
			System.out.println(a.getNumber());
			PreparedStatement ps1=con.prepareStatement("select * from contactlist where number=?");
			ps1.setString(1, a.getNumber());
			ResultSet rs=ps1.executeQuery();
			while(rs.next()) {
				if(a.getNumber().equals(rs.getString(3))) {
					check=1;
				}
			}
			if(check==0) {
			PreparedStatement ps=con.prepareStatement("insert into contactlist (name,number,email) values(?,?,?)");
			ps.setString(1, a.getName());
			ps.setString(2, a.getNumber());
			ps.setString(3, a.getEmail());
			ps.executeUpdate();
			map.put("msg", "Added Successfully");
			}else {
				map.put("msg", "Number already exists");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public Map<String, String> deleteoneContact(String number) {
		Map<String, String> map=new HashMap();
		try {
			int check=0;
			System.out.println(number);
			PreparedStatement ps1=con.prepareStatement("select * from contactlist where number=?");
			ps1.setString(1, number);
			ResultSet rs=ps1.executeQuery();
			while(rs.next()) {
				System.out.println(rs.getString(3));
				if(number.equals(rs.getString(3))) {
					check=1;
				}
			}
			if(check==1) {
				PreparedStatement ps=con.prepareStatement("delete from contactlist where number=?");
				ps.setString(1, number);
				ps.executeUpdate();
				map.put("msg", "Deleted Successfully");
			}else {
				map.put("msg", "No entry found");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
		
	}
	
	public Map<String, String> updateoneContact(Contact a,String newnum) {
		Map<String, String> map=new HashMap();
		try {
			int check=0,check1=0;
			PreparedStatement ps=con.prepareStatement("select * from contactlist where number=?");
			ps.setString(1, a.getNumber());
			ResultSet rs=ps.executeQuery();
			while(rs.next()) {
				if(a.getNumber().equals(rs.getString(3))) {
					check=1;
				}
			}
			
			PreparedStatement p2=con.prepareStatement("select * from contactlist where number=?");
			p2.setString(1, newnum);
			ResultSet rs1=p2.executeQuery();
			while(rs1.next()) {
				if(a.getNumber().equals(rs1.getString(3))) {
					check1=1;
				}
			}
			
			if(check==1 && check1==0) {
				PreparedStatement ps1=con.prepareStatement("update contactlist set name=?,number=?,email=? where number=?");
				ps1.setString(1, a.getName());
				ps1.setString(2, newnum);
				ps1.setString(3, a.getEmail());
				ps1.setString(4, a.getNumber());
				map.put("msg", "update Successful");
			}else {
				map.put("msg", "update Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("msg", "update Failed");
		}
		return map;
	}
	
}
