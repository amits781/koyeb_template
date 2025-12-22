package com.example.demo.service;


import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.demo.dto.EmailRequestBody;
import com.example.demo.dto.EmailResponseBody;
import com.example.demo.exception.ServiceAPIException;
import com.example.demo.parser.BankEmailParser;


@Service
public class TransactionService {

    private final List<BankEmailParser> parsers;

    public TransactionService(List<BankEmailParser> parsers) {
        this.parsers = parsers;
    }

    public EmailResponseBody processEmail(EmailRequestBody request) {
        return parsers.stream()
                .filter(parser -> parser.canParse(request))
                .findFirst()
                .map(parser -> parser.parse(request))
            .orElseThrow(() -> new ServiceAPIException("No parser found for this email format",
                HttpStatus.BAD_REQUEST));
    }
}