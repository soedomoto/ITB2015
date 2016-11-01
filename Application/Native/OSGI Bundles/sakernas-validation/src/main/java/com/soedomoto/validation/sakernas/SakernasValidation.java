package com.soedomoto.validation.sakernas;

import com.google.common.collect.Multimap;
import com.soedomoto.validation.core.service.IValidation;

/**
 * Created by soedomoto on 10/31/16.
 */
public class SakernasValidation implements IValidation {
    public void validate(Multimap entry) {
        System.out.println("Validate is done");
    }
}
