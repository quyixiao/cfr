package org.benf.cfr.reader.util.output;

import org.benf.cfr.reader.bytecode.analysis.parse.utils.QuotingUtils;
import org.benf.cfr.reader.bytecode.analysis.types.JavaTypeInstance;
import org.benf.cfr.reader.state.TypeUsageInformation;
import org.benf.cfr.reader.util.collections.SetFactory;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;

import java.util.List;
import java.util.Set;

public abstract class StreamDumper implements Dumper {
    private final TypeUsageInformation typeUsageInformation;
    private final IllegalIdentifierDump illegalIdentifierDump;
    private final boolean convertUTF;

    private int outputCount = 0;
    private int indent;
    private boolean atStart = true;
    private boolean pendingCR = false;
    private final Set<JavaTypeInstance> emitted = SetFactory.newSet();

    public StreamDumper(TypeUsageInformation typeUsageInformation, Options options, IllegalIdentifierDump illegalIdentifierDump) {
        this.typeUsageInformation = typeUsageInformation;
        this.illegalIdentifierDump = illegalIdentifierDump;
        this.convertUTF = options.getOption(OptionsImpl.HIDE_UTF8);
    }

    @Override
    public TypeUsageInformation getTypeUsageInformation() {
        return typeUsageInformation;
    }

    protected abstract void write(String s);

    @Override
    public void printLabel(String s) {
        processPendingCR();
        write(s + ":\n");
        atStart = true;
    }

    @Override
    public void enqueuePendingCarriageReturn() {
        pendingCR = true;
    }

    @Override
    public Dumper removePendingCarriageReturn() {
        pendingCR = false;
        atStart = false;
        return this;
    }

    private void processPendingCR() {
        if (pendingCR) {
            write("\n");
            atStart = true;
            pendingCR = false;
        }
    }

    @Override
    public Dumper identifier(String s) {
        return print(illegalIdentifierDump.getLegalIdentifierFor(s));
    }

    @Override
    public Dumper print(String s) {
        processPendingCR();
        doIndent();
        boolean doNewLn = false;
        if (s.endsWith("\n")) {
            s = s.substring(0, s.length() - 1);
            doNewLn = true;
        }
        if (convertUTF) s = QuotingUtils.enquoteUTF(s);
        write(s);
        atStart = false;
        if (doNewLn) {
            newln();
        }
        outputCount++;
        return this;
    }

    @Override
    public Dumper print(char c) {
        return print("" + c);
    }

    @Override
    public Dumper newln() {
        if (pendingCR) write("\n");
        pendingCR = true;
        atStart = true;
        outputCount++;
        return this;
    }

    @Override
    public Dumper endCodeln() {
        write(";");
        pendingCR = true;
        atStart = true;
        outputCount++;
        return this;
    }

    private void doIndent() {
        if (!atStart) return;
        String indents = "    ";
        for (int x = 0; x < indent; ++x) write(indents);
        atStart = false;
    }

    @Override
    public int getIndent() {
        return indent;
    }

    @Override
    public void indent(int diff) {
        indent += diff;
    }

    @Override
    public void dump(List<? extends Dumpable> d) {
        for (Dumpable dumpable : d) {
            dumpable.dump(this);
        }
    }

    @Override
    public Dumper dump(JavaTypeInstance javaTypeInstance) {
        javaTypeInstance.dumpInto(this, typeUsageInformation);
        return this;
    }

    @Override
    public Dumper dump(Dumpable d) {
        if (d == null) {
            return print("null");
        }
        return d.dump(this);
    }

    @Override
    public boolean canEmitClass(JavaTypeInstance type) {
        return emitted.add(type);
    }

    @Override
    public int getOutputCount() {
        return outputCount;
    }
}
