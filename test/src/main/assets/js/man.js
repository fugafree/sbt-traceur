import { Human } from "./human.js";

export class Man extends Human {

    constructor(firstname, lastname, birthday) {
        super(firstname, lastname, birthday);
        this.sex = true;
    }

}