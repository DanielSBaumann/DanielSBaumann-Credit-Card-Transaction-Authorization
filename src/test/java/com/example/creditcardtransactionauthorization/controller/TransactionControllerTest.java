package com.example.creditcardtransactionauthorization.controller;

import com.example.creditcardtransactionauthorization.dto.TransactionResponse;
import com.example.creditcardtransactionauthorization.model.Transaction;
import com.example.creditcardtransactionauthorization.service.AuthorizationService;
import com.example.creditcardtransactionauthorization.util.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorizationService authorizationService;

    @Test
    void testAuthorizeTransaction() throws Exception {
        TransactionResponse response = new TransactionResponse(Constants.CODE_APPROVED);
        when(authorizationService.authorizeTransaction(any(Transaction.class))).thenReturn(response);

        String transactionJson = "{\"accountId\":\"123\",\"amount\":50.0,\"mcc\":\"5411\",\"merchant\":\"Some Merchant\"}";

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Constants.CODE_APPROVED));
    }
}
