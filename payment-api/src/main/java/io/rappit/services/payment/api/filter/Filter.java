package io.rappit.services.payment.api.filter;

public interface Filter {

//    Filter or(Filter orFilter);
//    Filter and(Filter... field);

    String name();
    Condition condition();
    Object value();

    enum Condition {
        EQUALS,
        NOT_EQUALS,
        GREATER_THEN,
        LESS_THEN,
        LIKE
    }

    class Equals implements Filter {
        private final String name;
        private final String value;

        public Equals(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Condition condition() {
            return Condition.EQUALS;
        }

        @Override
        public Object value() {
            return value;
        }
    }
}
