# aim.thrift

namespace java com.abiquo.aimstub

struct Datastore
{
    1:string device;
    2:string path;
    3:string type;
    4:i64 totalSize;
    5:i64 usableSize;
}

struct NetInterface
{
    1:string name;
    2:string address;
    3:string physicalAddress;    	
}

struct NodeInfo
{
    1:string name;
    2:i64 version;
    3:i32 cores;
    4:i32 sockets;
    5:double memory;
}

enum DomainState
{
    ON = 1,
    OFF = 2,
    PAUSED = 3,
    UNKNOWN = 4,
}

struct DomainInfo
{
    1:string name;
    2:string uuid;
    3:DomainState state
    4:i32 numberVirtCpu;
    5:double memory;
    6:string xmlDesc;
}

struct Datapoint
{
    1:i32 timestamp;
    2:i64 value;
}

struct Measure
{
    1:string metric;
    2:list<Datapoint> datapoints;
    3:map<string, string> dimensions;
}

struct DomainBlockInfo
{
    1:i64 capacity;
    2:i64 allocation;
    3:i64 physical;
    4:string diskPath;
}

struct BinaryFile
{
    1:binary data;
}

exception RimpException
{
    1:string description;
}

exception VLanException
{
    1:string description;
}

exception StorageException
{
    1:string description;
}

exception LibvirtException
{
    1:i32 code;
    2:i32 domain;
    3:string msg;
    4:i32 level;
    5:string str1;
    6:string str2;
    7:string str3;
    8:i32 int1;
    9:i32 int2;
}

service Aim
{
    /** Rimp procedures */
    void checkRimpConfiguration() throws (1:RimpException re),
    i64 getDiskFileSize(1:string virtualImageDatastorePath) throws (1:RimpException re),
    list<Datastore> getDatastores() throws (1:RimpException re),
    list<NetInterface> getNetInterfaces() throws (1:RimpException re),    
    void copyFromRepositoryToDatastore(1:string virtualImageRepositoryPath, 2:string datastorePath, 3:string virtualMachineUUID) throws (1:RimpException re),
    void deleteVirtualImageFromDatastore(1:string datastorePath, 2:string virtualMachineUUID) throws (1:RimpException re),
    void copyFromDatastoreToRepository(1:string virtualMachineUUID, 2:string snapshot, 3:string destinationRepositoryPath, 4:string sourceDatastorePath) throws (1:RimpException re),
    void instanceDisk(1:string source, 2:string destination) throws (1:RimpException re),
    void renameDisk(1:string oldPath, 2:string newPath) throws (1:RimpException re),

    /** VLan procedures */
    void createVLAN (1:i32 vlanTag, 2:string vlanInterface, 3:string bridgeInterface) throws (1:VLanException ve),
    void deleteVLAN (1:i32 vlanTag, 2:string vlanInterface, 3:string bridgeInterface) throws (1:VLanException ve),
    void checkVLANConfiguration() throws (1:VLanException ve),

    /** Storage configuration procedures */
    string getInitiatorIQN() throws (1:StorageException se),
    void rescanISCSI(1:list<string> targets) throws (1:StorageException se),

    /** Libvirt procedures */
    
    // Node related methods
    NodeInfo getNodeInfo() throws (1:LibvirtException libvirtException),
    
    // Domain related methods
    void defineDomain(1:string xmlDesc) throws (1:LibvirtException libvirtException),
    void undefineDomain(1:string domainName) throws (1:LibvirtException libvirtException),
    bool existDomain(1:string domainName),
    DomainState getDomainState(1:string domainName) throws (1:LibvirtException libvirtException),
    DomainInfo getDomainInfo(1:string domainName) throws (1:LibvirtException libvirtException),
    list<DomainInfo> getDomains() throws (1:LibvirtException libvirtException),
    
    // Change state related methods
    void powerOn(1:string domainName) throws (1:LibvirtException libvirtException),
    void powerOff(1:string domainName) throws (1:LibvirtException libvirtException),
    void shutdown(1:string domainName) throws (1:LibvirtException libvirtException),
    void reset(1:string domainName) throws (1:LibvirtException libvirtException),
    void pause(1:string domainName) throws (1:LibvirtException libvirtException),
    void resume(1:string domainName) throws (1:LibvirtException libvirtException),
    
    // Storage related methods
    void createISCSIStoragePool(1:string name, 2:string host, 3:string iqn, 4:string targetPath) throws (1:LibvirtException libvirtException),
    void createNFSStoragePool(1:string name, 2:string host, 3:string dir, 4:string targetPath) throws (1:LibvirtException libvirtException),
    void createDirStoragePool(1:string name, 2:string targetPath) throws (1:LibvirtException libvirtException),
    void createDisk(1:string poolName, 2:string name, 3:double capacityInKb, 4:double allocationInKb, 5:string format) throws (1:LibvirtException libvirtException), 
    void deleteDisk(1:string poolName, 2:string name) throws (1:LibvirtException libvirtException),
    void resizeVol(1:string poolName, 2:string name, 3:double capacityInKb) throws (1:LibvirtException libvirtException),
    void resizeDisk(1:string domainName, 2:string diskPath, 3:double diskSizeInKb) throws (1:LibvirtException libvirtException),
    DomainBlockInfo getDomainBlockInfo(1:string domainName, 2:string diskPath) throws (1:LibvirtException libvirtException),
    
    // Metric related methods
    list<Measure> getDatapoints(1:string domainName, 2:i32 timestamp),

    void upload(1:BinaryFile file, 2:string path);
}
