package com.example.crappycalculator

enum class Token {
    E {
        override fun toString() = "e"
    },
    PI {
        override fun toString() = "π"
    },
    FACT {
        override fun toString() = "!"
    },
    MOD {
        override fun toString() = "%"
    },
    SIN {
        override fun toString() = "sin"
    },
    COS {
        override fun toString() = "cos"
    },
    TAN {
        override fun toString() = "tan"
    },
    ASIN {
        override fun toString() = "asin"
    },
    ACOS {
        override fun toString() = "acos"
    },
    ATAN {
        override fun toString() = "atan"
    },
    LN {
        override fun toString() = "log"
    },
    DIV {
        override fun toString() = "/"
    },
    MUL {
        override fun toString() = "*"
    },
    SUB {
        override fun toString() = "-"
    },
    ADD {
        override fun toString() = "+"
    },
    SQRT {
        override fun toString() = "sqrt"
    },
    LBRA {
        override fun toString() = "("
    },
    RBRA {
        override fun toString() = ")"
    },
    EXP {
        override fun toString() = "^"
    },
    _7 {
        override fun toString() = "7"
    },
    _8 {
        override fun toString() = "8"
    },
    _9 {
        override fun toString() = "9"
    },
    _4 {
        override fun toString() = "4"
    },
    _5 {
        override fun toString() = "5"
    },
    _6 {
        override fun toString() = "6"
    },
    _1 {
        override fun toString() = "1"
    },
    _2 {
        override fun toString() = "2"
    },
    _3 {
        override fun toString() = "3"
    },
    _0 {
        override fun toString() = "0"
    },
    PERIOD {
        override fun toString() = "."
    };

    abstract override fun toString(): String
}