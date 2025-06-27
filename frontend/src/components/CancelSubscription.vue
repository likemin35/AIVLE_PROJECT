<template>

    <v-card outlined>
        <v-card-title>
            CancelSubscription
        </v-card-title>

        <v-card-text>
            <Boolean label="IsPurchase" v-model="value.isPurchase" :editMode="editMode"/>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="cancelSubscription"
            >
                CancelSubscription
            </v-btn>
            
            <v-btn
                    color="deep-purple lighten-2"
                    text
                    @click="close"
            >
                Close
            </v-btn>
        </v-card-actions>
    </v-card>

</template>

<script>

export default {
    name: 'CancelSubscriptionCommand',
    components:{
    },
    props: {},
    data: () => ({
        editMode: true,
        value: {},
    }),
    created() {
        this.value.isPurchase = false;
    },
    watch: {
    },
    methods: {
        cancelSubscription() {
            this.$emit('cancelSubscription', this.value);
        },
        close() {
            this.$emit('closeDialog');
        },
        change() {
            this.$emit("update:modelValue", this.value);
        },
    }
}
</script>

