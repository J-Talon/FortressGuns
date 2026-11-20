package me.camm.productions.fortressguns.Util;

//o = operand
//t = context

@FunctionalInterface
public interface Operator2<O, T> {

    public void accept(O o, T t);

}
