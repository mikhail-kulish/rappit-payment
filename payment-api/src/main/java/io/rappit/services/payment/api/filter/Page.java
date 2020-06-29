package io.rappit.services.payment.api.filter;

import io.rappit.common.media.Media;
import io.rappit.common.media.Printable;

import java.util.Collection;

public interface Page<T extends Printable> extends Printable {
    Long total();

    Integer number();

    Collection<T> content();

    @Override
    default void print(Media media) {
        media.with("total", total())
                .with("number", number())
                .with("content", content());
    }

    class Simple<T extends Printable> implements Page<T>{
        private final Long total;
        private final Integer number;
        private final Collection<T> content;

        public Simple(Long total, Collection<T> content) {
            this(total, content.size(), content);
        }

        public Simple(Long total, Integer number, Collection<T> content) {
            this.total = total;
            this.number = number;
            this.content = content;
        }

        @Override
        public Long total() {
            return total;
        }

        @Override
        public Integer number() {
            return number;
        }

        @Override
        public Collection<T> content() {
            return content;
        }
    }
}
