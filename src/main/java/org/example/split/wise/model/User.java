package org.example.split.wise.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class User {
    private int id;
    private String name;
    private String email;
    private final List<Split> splits = Collections.synchronizedList(new ArrayList<>());

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void addSplit(Split split) {
        splits.add(split);
    }
}
