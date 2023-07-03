#include <fcntl.h>
#include <unistd.h>
#include <termios.h>
#include "PowerDevice.h"

#define IODEV_IOC_MAGIC  'H'
#define IODEV_IOCTL_SCAN_TRIG       _IOW(IODEV_IOC_MAGIC, 3, int)
#define IODEV_IOCTL_SCAN_PWRDWN     _IOW(IODEV_IOC_MAGIC, 4, int)
#define IODEV_IOCTL_SCAN_WAKEUP     _IOW(IODEV_IOC_MAGIC, 5, int)
#define IODEV_IOCTL_SCAN_POWER      _IOW(IODEV_IOC_MAGIC, 6, int)
#define IODEV_IOCTL_TRANSFORM_EN    _IOW(IODEV_IOC_MAGIC, 13, int)

PowerDevice::PowerDevice() {
    mPowerFd = open("/dev/iodev", O_RDWR);
}

PowerDevice::~PowerDevice() {
    if (mPowerFd > 0) {
        close(mPowerFd);
        mPowerFd = 0;
    }
}

int PowerDevice::scannerPower(int on) const {
    if (mPowerFd <= 0)
        return -1;

    ioctl(mPowerFd, IODEV_IOCTL_SCAN_POWER, &on);
    if(on > 0)
        usleep(1000 * 100);

    return scannerTransform(on);
}

int PowerDevice::scannerWakeup(int on) const {
    if (mPowerFd <= 0)
        return -1;

    return ioctl(mPowerFd, IODEV_IOCTL_SCAN_WAKEUP, &on);
}

int PowerDevice::scannerPowerDwn(int on) const {
    if (mPowerFd <= 0)
        return -1;

    return ioctl(mPowerFd, IODEV_IOCTL_SCAN_PWRDWN, &on);
}

int PowerDevice::scannerTrig(int trig) const {
    if (mPowerFd <= 0)
        return -1;

    return ioctl(mPowerFd, IODEV_IOCTL_SCAN_TRIG, &trig);
}

int PowerDevice::scannerTransform(int on) const {
    return ioctl(mPowerFd, IODEV_IOCTL_TRANSFORM_EN, &on);
}
