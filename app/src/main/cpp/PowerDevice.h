#ifndef __POWER_DEVICE_H__
#define __POWER_DEVICE_H__

class PowerDevice {
private:
    int mPowerFd;

public:
    PowerDevice();

    virtual ~PowerDevice();

    int scannerPower(int on) const;

    int scannerTrig(int trig) const;

    int scannerWakeup(int on) const;

    int scannerPowerDwn(int on) const;

private:
    int scannerTransform(int on) const;
};


#endif //__POWER_DEVICE_H__
