export class Human {

    constructor(firstname, lastname, birthday) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = (birthday === undefined) ? null : birthday;
        this.sex = null;
    }

    sayHello() {
        const sex = (this.isMan()) ? "man" : "woman";

        return `Hello, my name is ${this.getFullname()} and I am a ${sex} of ${this.getAge()} years old.`;
    }

    getFullname() {
        return `${this.firstname} ${this.lastname}`;
    }

    getAge() {
        let ageDiff = Date.now() - this.birthday.getTime();
        let ageDate = new Date(ageDiff);

        return Math.abs(ageDate.getUTCFullYear() - 1970);
    }

    isMan() {
        return this.sex;
    }

    isWoman() {
        return !this.sex;
    }
}