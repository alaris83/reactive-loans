package com.handywork.loanchecker;

import com.handywork.loanchecker.client.dto.LoanDto;

/**
 * Test data shared library.
 *
 * @author Libor Mika (Libor.Mika@topmonks.com).
 */
public final class TestData {

    public static final LoanDto LOAD_DTO_1 = generateLoadDto(1);
    public static final LoanDto LOAD_DTO_2 = generateLoadDto(2);


    public static LoanDto generateLoadDto(int counter) {
        final LoanDto result = new LoanDto();
        result.setName("LoanDto name " + counter);
        result.setPurpose("LoanDto purpose " + counter);
        result.setStory("LoanDto story " + counter);
        result.setUrl("LoanDto url " + counter);
        return result;
    }
}