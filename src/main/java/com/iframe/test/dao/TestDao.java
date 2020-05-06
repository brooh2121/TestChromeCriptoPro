package com.iframe.test.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TestDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public int firstJourInsert(String tenderNumber, String docStep, boolean bolResult, String comment) {
    String query = "insert into send_doc_jur (\n" +
            "send_doc_id,\n" +
            "auction_number,\n" +
            "doc_step,\n" +
            "sucessful_step,\n" +
            "doc_send_comment\n" +
            ")\n" +
            "values(\n" +
            "(select nextval('send_doc_id_seq')),\n" +
            "'"+tenderNumber+"',\n" +
            "'"+docStep+"',\n" +
            ""+bolResult+",\n" +
            "'"+comment+"'\n" +
            ");";

    return jdbcTemplate.update(query);
    }
}
