/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsonparser;

/**
 *
 * @author ricardozun
 */
public class Data {
    String section;
    String serial_number;
    String query;
    String date_time;
    String message;
    String exception_detail;
    String exception_stack_trace;
    
    //construct
     public Data(String section, String serial_number, String query, String date_time, String message, String exception_detail, String exception_stack_trace) {
        this.section = section;
        this.serial_number = serial_number;
        this.query = query;
        this.date_time = date_time;
        this.message = message;
        this.exception_detail = exception_detail;
        this.exception_stack_trace = exception_stack_trace;
    }
     
     //gets and sets
    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getException_detail() {
        return exception_detail;
    }

    public void setException_detail(String exception_detail) {
        this.exception_detail = exception_detail;
    }

    public String getException_stack_trace() {
        return exception_stack_trace;
    }

    public void setException_stack_trace(String exception_stack_trace) {
        this.exception_stack_trace = exception_stack_trace;
    } 
    
}
