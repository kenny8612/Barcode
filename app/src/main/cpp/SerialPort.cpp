#include <unistd.h>
#include <fcntl.h>
#include <cstring>
#include <termio.h>
#include <android/log.h>
#include "SerialPort.h"

#define LOG_TAG "SERIAL_PORT"
#define DBG(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define ERR(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

SerialPort::SerialPort() {
    mSerial = 0;
}

SerialPort::~SerialPort() {
    if (mSerial > 0)
        ::close(mSerial);
}

bool SerialPort::open(const char *serial_path, int baud) {
    mSerial = ::open(serial_path, O_RDWR | O_NONBLOCK);
    if (mSerial < 0) {
        ERR("native_open serial \'%s\' failed.", serial_path);
        return false;
    }
    config(baud);
    DBG("open serial %s", serial_path);
    return true;
}

void SerialPort::close() {
    if (mSerial <= 0)
        return;

    ::close(mSerial);
    DBG("close serial %d", mSerial);
    mSerial = 0;
}

void SerialPort::config(int baud) const {
    struct termios options{};
    speed_t sBaud;

    switch (baud) {
        case 4800:
            sBaud = B4800;
            break;
        case 9600:
            sBaud = B9600;
            break;
        case 19200:
            sBaud = B19200;
            break;
        case 38400:
            sBaud = B38400;
            break;
        case 57600:
            sBaud = B57600;
            break;
        case 115200:
            sBaud = B115200;
            break;
        case 230400:
            sBaud = B230400;
            break;
        case 460800:
            sBaud = B460800;
            break;
        case 500000:
            sBaud = B500000;
        case 576000:
            sBaud = B576000;
        case 921600:
            sBaud = B921600;
        default:
            sBaud = B9600;
    }

    tcgetattr(mSerial, &options);
    cfmakeraw(&options);
    cfsetispeed(&options, sBaud);
    cfsetospeed(&options, sBaud);
    options.c_cflag |= (CLOCAL | CREAD);
    options.c_cflag &= ~PARENB;
    options.c_cflag &= ~CSTOPB;
    options.c_cflag &= ~CSIZE;
    options.c_cflag &= ~PARODD;
    options.c_cflag |= CS8;
    options.c_cflag &= ~CRTSCTS;
    options.c_lflag &= ~(ISIG | ECHO);
    options.c_oflag &= ~OPOST;
    options.c_iflag &= ~(INLCR | INPCK | ISTRIP | IXON | BRKINT);
    //options.c_cc[VMIN] = 1;
    //options.c_cc[VTIME] = 0;
    tcsetattr(mSerial, TCSANOW, &options);
    tcflush(mSerial, TCIOFLUSH);
}

ssize_t SerialPort::write(const void *const buf, size_t count) const {
    ssize_t len = 0;

    if (mSerial > 0) {
        len = ::write(mSerial, buf, count);
        tcdrain(mSerial);
    }

    return len;
}

ssize_t SerialPort::read_nb(void *const buf, size_t count, int timeout_ms, char end) const {
    char *p = static_cast<char *>(buf);
    size_t remainCount = count;
    ssize_t len, pos = 0;
    fd_set rds;
    timeval tv = {0};
    int ret;

    while (true) {
        FD_ZERO(&rds);
        FD_SET(mSerial, &rds);
        tv.tv_sec = timeout_ms / 1000;
        tv.tv_usec = (timeout_ms % 1000) * 1000;
        ret = select(mSerial + 1, &rds, nullptr, nullptr, &tv);
        if (ret <= 0)
            break;

        len = read(mSerial, p + pos, remainCount);
        if (len < 0)
            continue;

        if (len == remainCount) {
            pos += len;
            break;
        }
        remainCount -= len;
        pos += len;
        if (*(p + pos - 1) == end)
            break;
    }

    return pos;
}

void SerialPort::flush() const {
    if (mSerial > 0)
        tcflush(mSerial, TCIOFLUSH);
}
