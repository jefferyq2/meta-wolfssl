SUMMARY = "wolfSSL Lightweight Embedded SSL/TLS Library"
DESCRIPTION = "wolfSSL is a lightweight SSL/TLS library written in C and optimized for embedded and RTOS environments. It supports a full TLS client and server, up to TLS 1.3."
HOMEPAGE = "https://www.wolfssl.com/products/wolfssl/"
BUGTRACKER = "https://github.com/wolfssl/wolfssl/issues"
SECTION = "libs"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
DEPENDS += "util-linux-native"

PROVIDES += "wolfprovider"
RPROVIDES_${PN} = "wolfprovider"

SRC_URI = "git://github.com/wolfssl/wolfProvider.git;protocol=https;branch=master"
SRCREV = "${AUTOREV}"
S = "${WORKDIR}/git"

DEPENDS += " wolfssl \
            openssl \
            "

inherit autotools pkgconfig

OPENSSL_YOCTO_DIR = "${COMPONENTS_DIR}/${PACKAGE_ARCH}/openssl/usr"

# Approach: Use Python to dynamically set function content based on Yocto version
python() {
    distro_version = d.getVar('DISTRO_VERSION', True)
    autogen_command = "cd ${S}; ./autogen.sh"
    if distro_version and (distro_version.startswith('2.') or distro_version.startswith('3.')):
        # For Dunfell and earlier
        d.appendVar('do_configure_prepend', autogen_command)
    else:
        # For Kirkstone and later
        d.appendVar('do_configure:prepend', autogen_command)
}

CFLAGS += " -I${S}/include -g0 -O2 -ffile-prefix-map=${WORKDIR}=."
CXXFLAGS += " -I${S}/include  -g0 -O2 -ffile-prefix-map=${WORKDIR}=."
LDFLAGS += " -Wl,--build-id=none"
EXTRA_OECONF += " --with-openssl=${OPENSSL_YOCTO_DIR}"
