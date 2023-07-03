#ifndef __SERIAL_PORT_H_
#define __SERIAL_PORT_H_

class SerialPort {
private:
    int mSerial;

private:
    void config(int baud) const;

public:
    SerialPort();

    virtual ~SerialPort();

    bool open(const char *serial_path, int baud);

    void close();

    ssize_t write(const void *const buf, size_t count) const;

    ssize_t read_nb(void *const buf, size_t count, int timeout_ms, char end) const;

    void flush() const;

    inline bool isTurnOn() { return mSerial > 0; }
};


#endif //__SERIAL_PORT_H_
