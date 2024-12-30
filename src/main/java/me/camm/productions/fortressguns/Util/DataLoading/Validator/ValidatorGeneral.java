package me.camm.productions.fortressguns.Util.DataLoading.Validator;

import me.camm.productions.fortressguns.Util.DataLoading.Schema.ConfigGeneral;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.DoublePredicate;

public class ValidatorGeneral implements Validator<ConfigGeneral>{
    @Override
    public boolean validate(@NotNull ConfigGeneral in) {

        double[] values = in.getDoubleValues();
        double missileDifficulty = in.getMissileDifficulty();
        boolean invalidValue = Arrays.stream(values).anyMatch(new DoublePredicate() {
            @Override
            public boolean test(double value) {
                return value <= 0;
            }
        });

        return missileDifficulty <= 100 && missileDifficulty >= 1 && !(invalidValue);


    }
}
