package io.rappit.services.payment.api;

import io.rappit.common.media.Media;
import io.rappit.common.media.Printable;
import io.rappit.common.world.location.Location;
import io.rappit.common.world.people.Name;

import java.util.Optional;

public interface Payer extends Printable {
    String email();
    Name name();
    Optional<Location> billing();

    @Override
    default void print(Media media) {
        media
                .with("email", email())
                .with("name", name());
        billing().ifPresent(billing -> media.with("billing", billing));
    }

    class Simple implements Payer {
        private final String email;
        private final Name name;
        private final Location billing;

        public Simple(String email, Name name, Location billing) {
            this.email = email;
            this.name = name;
            this.billing = billing;
        }

        public Simple(String email, Name name) {
            this(email, name, null);
        }

        @Override
        public String email() {
            return email;
        }

        @Override
        public Name name() {
            return name;
        }

        @Override
        public Optional<Location> billing() {
            return Optional.ofNullable(billing);
        }
    }
}
