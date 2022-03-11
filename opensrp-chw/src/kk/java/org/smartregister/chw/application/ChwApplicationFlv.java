package org.smartregister.chw.application;

public class ChwApplicationFlv extends DefaultChwApplicationFlv {
    @Override
    public boolean useThinkMd() {
        return false;
    }

    @Override
    public boolean hasP2P() {
        return false;
    }
}
