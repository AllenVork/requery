# Requery For Android

---
## Quickstart
### Prerequisites
> + Android Studio 1.5 及以上
> - Java JDK 8
    
### Installation
**Step1:** 将下面的依赖添加到 module 中的 build.gradle 中
```gradle
// Requery
compile 'io.requery:requery:1.1.0'
compile 'io.requery:requery-android:1.1.0'
annotationProcessor 'io.requery:requery-processor:1.1.0'

// Rxjava 可选
compile 'io.reactivex:rxandroid:1.2.1'
compile 'io.reactivex:rxjava:1.1.6'
```
**Step2:** 如果你的项目中使用 Lint 去检测代码的话，需要让其忽略 InvalidPackage 异常（因为 Requery 中有一些对 android 无用的类）
```gradle
android {
    lintOptions {
        disable 'InvalidPackage'
    }
}
```
---

## Getting Started
### Configuration    
　　首先对要创建数据库，表等。这步操作在 MX4 pro 上测试时间大致为 77ms, 所以最好放在异步执行。
```java
//最后一个参数为数据库的版本号
DatabaseSource source = new DatabaseSource(this, Models.DEFAULT, 1);

Configuration configuration = source.getConfiguration();

SingleEntityStore<Persistable> data = RxSupport.toReactiveStore(
        new EntityDataStore<Persistable>(configuration));
```
**DatabaseSource**    
　　DatabaseSource 继承自 SqliteOpenHelper，它可以为你自动创建表。你需要传入数据库的版本号，你也可以选择传入数据库名之类的信息。默认会创建一个名为”default”的数据库。    
　　第二个参数 Models.DEFAULT 是你在编译项目后由 Requery 自动生成的。在你新建表之后，编译一下项目就会生成一些代理类以及这个 Models 类，它保存了所需要创建表的信息给 DatabaseSource 去建表。

**SingleEntityStore**    
　　EntityDataStore 为 Requery 提供的数据库操纵类，提供 update , select 等方法，这些方法都是阻塞式的。而 SingleEntityStore 为 Requery 结合 Rxjava1 所提供的异步操作数据库的类。它是将同步操作放到 Rxjava 的 Single 里面去做。你可以在 RxSupport.toReactiveStore() 的最后一个参数中传线程信息，如 Schedulers.io() 之类，默认是在一个单线程的线程池中进行操作。    

**Persistable**    
　　我们看到`SingleEntityStore<Persistable>　data`，Persistable 代表通过 data 操作数据库后所返回值的类型。如果不使用的话，使用 data.select().get() 将会返回 Object 对象，这时如果要进行toObservable()等操作的话，注意要强制转换一下：
```java
RxResult<Person> result = (RxResult) data.select(Person.class).get();
result.toObservable().subscribe(...)
```
　　
### Create Table    
　　Requery 有3种方式进行建表，这里讲解2种常用的方式：

　　**1.通过接口创建**
```java
@Entity(name = "PersonProxy")
public interface Person {

    @Key @Generated
    int getId();
    
    String getName();
    String getEmail();
    Date getBirthday();
    int getAge();

    @OneToOne
    @ForeignKey
    @Column(name = "ad")
    Address getAddress();

    UUID getUUID();

    @OneToMany(mappedBy = "owner", cascade = {CascadeAction.DELETE, CascadeAction.SAVE})
    List<Phone> getPhoneNumberList();
}
```
　　**2.通过抽象类进行创建**
```java
@Entity
abstract class AbstractPerson {

    @Key @Generated
    int id;

    @Index("name_index")                     // table specification
    String name;

    @OneToMany                               // relationships 1:1, 1:many, many to many
    Set<Phone> phoneNumbers;

    @Convert(EmailToStringConverter.class) // custom type conversion
    Email email;

    @PostLoad                                // lifecycle callbacks
    void afterLoad() {
        updatePeopleList();
    }
    // getter, setters, equals & hashCode automatically generated into Person.java
}
```

你可能有如下疑问：    
1.为什么接口里面是方法而抽象类中是变量？    
　　首先要弄清楚的是无论是接口还是抽象类，里面的数据是为了创建表中的 column 的。所以只需要用变量就行了，但是接口里面的变量是必须要初始化的，所以用 set 和 get 方法。编译器会自动提取 set×××() 中的×××作为表中的 Column（不是get)。get 方法你按需设置。

2.上面有很多注解，都是什么鬼?    
　　**@Entity** 建表必须字段，标志这个类即为要创建的表。    
　　**@Key** 主键。    
　　**@Generated** 代表该字段是自动生成的，与 @Key 一起使用。   
　　**@Index** 索引。    
　　**@OneToMany @OneToOne等** 表示对应关系，一对一，多对一等。     
　　**@Convert** 转换器。由于数据库中只能存 int 之类的基本类型，如果你存一些不兼容的类型，如对象之类的，可以用 Converter 将该对象转换成基本类型存入表中。后面会详细讲。    
　　**@ForeignKey** 外键。我们看一下代码：
```java
@Entity(name = "PersonProxy")
public interface Person {

    @OneToOne
    @ForeignKey
    Address getAddress();
}
```
```java
@Entity
public interface Address {}
```
　　表（Person）中包含另一张表（Address）的话，可以用 @ForeignKey 注解（Address），那么 Person 表中会有一个”address”列。当往 Person 表中插入数据时，里面的 Address 会自动插入到 Address 表中，然后将主键存到Person表中。默认是用主键作为外键的（因为主键是唯一的），如果用另一个 column 的话，需要设置该 column 的 unique 为 true 。     
　　**@Column** 你可以指定该列的一些信息，譬如“unique”，“index”，“name”等。指定表中该 column 的 name 是什么。譬如上面 Address getAddress() 。如果不指定 Column 的 name 的话，表中的该列为“address”，指定 @Column(name = "ad") 的话，就为“ad”而不是“address”了。     
　　还有很多注解，就不在这里一一解释了。      

3.我们看到上面 Person 中有 Address 表和 Phone 表，怎么上面的注解都不一样啊？麻痹！    
　　亲，亲，不一样是因为 Person 中只有一个 Address 而 Phone 却是一个列表。上面讲过 @ForeignKey 的列 默认会存关联的表的主键（或者其余的唯一的 column）。如果你有 n 个该对象的话，就不能用 @OneToOne 和 @ForeignKey了，而是用 @OneToMany 之类。我们来看一下代码：
```java
@Entity(name = "PersonProxy")
public interface Person {

    @OneToMany(mappedBy = "owner", cascade = {CascadeAction.DELETE, CascadeAction.SAVE})
    List<Phone> getPhoneNumberList();
}
```
```java
@Entity
public interface Phone {

    @ManyToOne
    Person getOwner();
}
```
　　mappedBy = "owner" 即为 Phone 表中的 Person getOwner() 的 owner，这个可以省略，它可以自动映射到。这样 Phone 表中的 owner 字段中存 Person 的主键，而 Person 表中不会添加 phone 这一列的。     
　　若使用 @ManyToMany 字段的话，需要使用 @JunctionTable 来指定附属关系。
```java
@Entity
public abstract class Person {

    @ManyToMany
    @JunctionTable 
    List<Phone> phone;
}

@Entity
public abstract class Phone {

    @ManyToMany
    List<Person> person;
}
```
　　在 List<Phone> phone 上使用 @JunctionTable 代表 Person 才是主表，那么可以通过 Person 往 Phone 表中插入数据，而 Phone 表不能往 Person 表中插入。和 @OneToOne 以及 @OneToMany 不同的是它不会往 Person 表或者 Phone 表中添加任何相关的列，而是创建一个新表来存入这两张表的主键。

4.这个类里面如果只是关联一个普通的类而不是表该怎么操作，@ForeignKey @OneToMany 等仅仅针对外表而言的呀    
　　上面的描述可以用以下代码表述：
```java
@Entity(name = "PersonProxy")
public interface Person {

    @Convert(EmailConverter.class)
    List<Email> getEmail();
    void setEmail(List<Email> email);
}
```
```java
public class EmailConverter implements Converter<List<Email>, String> {

    private static final String SEPARATOR = "\0007";

    @Override
    public Class<List<Email>> getMappedType() {
        return (Class) List.class;
    }

    @Override
    public Class<String> getPersistedType() {
        return String.class;
    }

    @Nullable
    @Override
    public Integer getPersistedSize() {
        return null;
    }

    @Override
    public String convertToPersisted(List<Email> value) {
        if (value == null || value.size() <= 0) return null;

        StringBuilder sb = new StringBuilder();

        for (Email email : value) {
            sb.append(email.getEmail());
            sb.append(SEPARATOR);
        }

        return sb.toString();
    }

    @Override
    public List<Email> convertToMapped(Class<? extends List<Email>> type, String value) {
        if (TextUtils.isEmpty(value)) return Collections.emptyList();

        String[] emailArr = value.split(SEPARATOR);

        List<Email> emails = new ArrayList<>(emailArr.length);

        for (String str : emailArr) {
            Email email = new Email();
            email.setEmail(str);
            emails.add(email);
        }

        return emails;
    }
}
```
　　EmailConverter 会在写 insert 时将 List<Email> 转换成 String 存进去，在 select 的时候会将 String 转换成我们需要的 List<Email>。

5.为什么官方的例子里面的类都是实现了Persistable，你这里都没有？    
　　这里不写是为了避免误导大家。如果要实现Persistable的话，在下面代码中都要带上<Persistable>用于限定使用SingleEntityStore返回值的类型，那么在执行data.select().get()等操作就不会返回Object类型了。
```java
SingleEntityStore<Persistable> dataStore = RxSupport.toReactiveStore(new EntityDataStore<Persistable>(configuration))
```
##  Database Operations
　　数据库的操作是通过前面的`SingleEntityStore data`进行的。    
　　我们之前建过 Person，Address 等表。在编译后，会在项目的 build 目录里面生成 PersonEntity, AddressEntity（类名是可以通过 @Entity 注解里的 name 字段指定，上面我指定 Person 类生成的代理类的类名为 PersonProxy）等代理类，这个类里面会生成很多方法，包含 get 和 set 方法，所以在使用的时候使用这个代理类会比较方便。    
　　Person 生成的代理类为 PersonProxy，Address 和 Phone 因为未指定 name，所以默认为 AddressEntity，PhoneEntity。    
　　这里简单介绍 Requery 的基本操作，不再赘述 sql 语句的用法。

**Insert:**
```java
PersonProxy person = new PersonProxy();

person.setName("zhangruofan");

Phone phone = new PhoneEntity();
phone.setPhoneNumber("12354565");
//注意列表的插入方式
person.getPhoneNumberList().add(phone);

people.add(person);


//开始插入
data.insert(people)
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(...);
```
由于我们上面使用了 SingleEntityStore ，所以 data.insert(people) 默认就是在子线程中执行的，会返回一个 Single。    

**select / find**    
　　通过主键查找，返回单条数据
```java
data.findByKey(PersonProxy.class, “主键值”)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(...);
```
```java
data.select(AddressEntity.class)
        .where(AddressEntity.CITY.notLike("Hong Kong"))
        .and(AddressEntity.COUNTRY.eq("China"))
        .orderBy(AddressEntity.STATE.desc())
        .limit(5)
        .get()
        .toObservable()
        .subscribe(...);
```

**count**
```
data.count(Person.class).get().toSingle().subscribe(...);
```

**delete**
```java
//删除表中的某些数据
data.delete(AddressEntity.class)
        .where(AddressEntity.CITY.notLike("Hong Kong"))
        .and(AddressEntity.COUNTRY.eq("China"))
        .get()
        .toSingle().subscribe()
        
//删除数据库中所有数据
data.delete().get().toSingle().subscribe();

//删除Entity
data.select(PersonProxy.class, PersonProxy.NAME, PersonProxy.ID)
        .where(PersonProxy.ID.lt(5))
        .get()
        .toObservable()
        .subscribe(new CustomizeSubscriber<Person>() {
            @Override
            public void onNext(Person person) {
                //直接删除person
                data.delete(person).subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.e(TAG, "onDelete: count =" + data.count(Person.class).get().value());
                    }
                });
            }
});
```

**update**
```java
//更新表中的数据
data.update(Person.class)
        .set(PersonProxy.AGE, 22)
        .where(PersonProxy.ID.greaterThan(5))
        .get()
        .toSingle()
        .subscribe(...);
    }
```
**结合count,select, update**
```java
//首先查询表的行数，然后查询表中相应的数据，然后修改数据，再将修改后的数据写入数据库
data.count(Person.class).get().toSingle().subscribe(new Action1<Integer>() {
    @Override
    public void call(Integer integer) {
        data.select(PersonProxy.class, PersonProxy.ID, PersonProxy.NAME, PersonProxy.AGE)
                .get()
                .toObservable()
                //如果不用buffer的话，有多少个数据就会调用多少次onNext()，这里是将所有数据缓存起来，然后一次性发过去
                .buffer(integer)
                .subscribe(new CustomizeSubscriber<List<PersonProxy>>() {
                    @Override
                    public void onNext(List<PersonProxy> personProxies) {
                        for (PersonProxy person : personProxies) {
                            person.setAge(18);
                        }

                        //将查询出来的数据修改后，更新到数据库中
                        data.update(personProxies).subscribe(...);
                    }
                });
    }
});
```

## Encryption
```java
//在build.gradle中加入
compile 'net.zetetic:android-database-sqlcipher:3.5.4@aar'

//然后将DatabaseSource换成SqlCipherDatabaseSource就行了

//DatabaseSource source = new DatabaseSource(this, Models.DEFAULT, 1);
SqlCipherDatabaseSource source = new SqlCipherDatabaseSource(this, Models.DEFAULT, "database name", "password",  1);
```
　　当你进入/data/data目录去看数据库时发现已经打不开了。

## Migrations
　　DatabaseSource继承了SqliteOpenHelper并重载了onUpgrade()方法进行基本的数据库迁移。如果你只是增加表或者往表中新增字段的话，只需要修改一下数据库的版本号就行了，不需要进行任何操作。（由于sqlite本身是不支持修改表中的字段，删除字段等操作，所以Requery同样也不支持）
