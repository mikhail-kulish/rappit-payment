package io.rappit.services.payment.api.filter;

public interface Pageable {
    Integer number();

    Integer size();

    Filter filter();

    class Simple implements Pageable {
        private final Integer number;
        private final Integer size;

        public Simple(Integer number, Integer size) {
            this.number = number;
            this.size = size;
        }

        @Override
        public Integer number() {
            return number;
        }

        @Override
        public Integer size() {
            return size;
        }

        @Override
        public Filter filter() {
            return null;
        }
    }
}
