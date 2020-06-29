package io.rappit.services.payment.jdbc;

import io.rappit.common.world.location.Location;
import io.rappit.common.world.people.Name;
import io.rappit.services.payment.api.Payer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ResultSetPayer implements Payer {
    private final String email;
    private final Name name;
    private final Location billing;

    public ResultSetPayer(ResultSet resultSet) throws SQLException {
        this(resultSet, "");
    }

    public ResultSetPayer(ResultSet resultSet, String prefix) throws SQLException {
        this.email = resultSet.getString(prefix.concat("payer_email"));
        this.name = new Name.Simple(
                resultSet.getString(prefix.concat("payer_name_first")),
                resultSet.getString(prefix.concat("payer_name_last"))
        );
        this.billing = Optional.ofNullable(resultSet.getString(prefix.concat("payer_address"))).map(
                addr -> {
                    try {
                        return new Location.Simple(
                                resultSet.getString(prefix.concat("payer_address")),
                                resultSet.getString(prefix.concat("payer_address_city")),
                                resultSet.getString(prefix.concat("payer_address_postal")),
                                resultSet.getString(prefix.concat("payer_address_country")),
                                resultSet.getString(prefix.concat("payer_address_additional")),
                                resultSet.getString(prefix.concat("payer_address_state"))
                        );
                    } catch (final SQLException e) {
                        return null;
                    }
                }
        ).orElse(null);
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
