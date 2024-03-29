package org.benf.cfr.reader.bytecode.analysis.variables;

import org.benf.cfr.reader.util.output.Dumper;

public class NamedVariableFromHint implements NamedVariable {
    private String name;
    private int slot;
    private int idx;

    NamedVariableFromHint(String name, int slot, int idx) {
        this.name = name;
        this.slot = slot;
        this.idx = idx;
    }

    @Override
    public void forceName(String name) {
        this.name = name;
    }

    @Override
    public String getStringName() {
        return name;
    }

    @Override
    public Dumper dump(Dumper d) {
        return d.print(name);
    }

    @Override
    public boolean isGoodName() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NamedVariableFromHint that = (NamedVariableFromHint) o;

        if (slot != that.slot) return false;
        if (idx != that.idx) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + slot;
        result = 31 * result + idx;
        return result;
    }

    @Override
    public String toString() {
        return name + " (" + slot + ")";
    }
}
