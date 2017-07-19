export class Human {

    constructor(firstname, lastname, birthday) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = (birthday === undefined) ? null : birthday;
        this.sex = null;
    }

    sayHello() {
        return `Hello, my name is ${this.getFullname()} and I am a ${this.getSex()} of ${this.getAge()} years old.`;
    }

    getFullname() {
        return `${this.firstname} ${this.lastname}`;
    }

    getAge() {
        const ageDiff = Date.now() - this.birthday.getTime();
        const ageDate = new Date(ageDiff);

        return Math.abs(ageDate.getUTCFullYear() - 1970);
    }

    getSex() {
        return (this._isMan()) ? "man" : "woman";
    }

    _isMan() {
        return this.sex;
    }

    _isWoman() {
        return !this.sex;
    }
}