package com.mangostudio.anotherschoolproject.frontend.logic;

public enum CardType {
    NUMBER2("2"), NUMBER3("3"), NUMBER4("4"), NUMBER5("5"), NUMBER6("6"), NUMBER7("7"), NUMBER8("8"), NUMBER9("9"), NUMBER10("10"),
    ACE("A"), JACK("J"), QUEEN("Q"), KING("K");

    public String identifier;

    CardType(String identifier) {
        this.identifier = identifier;
    }
}
