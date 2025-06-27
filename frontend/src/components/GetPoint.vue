<template>
    <v-card outlined style="border-radius: 0; margin-top: 10px; padding-bottom: 10px; background-color: #F4F5FA;">
        <v-card-title>
            포인트조회
        </v-card-title>
    
        <v-card-text style="margin-top:-10px;">
            <v-row no-gutters style="margin: 15px 0px -10px -15px;">
                <v-col style="max-width:140px;">
                    <Number class="attributes-list" label="Id" v-model="value.parameters.id" :editMode="editMode"/>
                </v-col>
                <v-col style="max-width:140px;">
                    <Number class="attributes-list" label="Point" v-model="value.parameters.point" :editMode="editMode"/>
                </v-col>
                <v-col style="max-width:140px;">
                    <Boolean class="attributes-list" label="구독권여부" v-model="value.parameters.isSubscribe" :editMode="editMode"/>
                </v-col>
                <v-col style="max-width:150px;">
                    <UserId style="margin-top: 0px; margin-left: 10px;" offline label="구독자 ID" v-model="value.parameters.userId" :editMode="editMode" @change="change"/>
                </v-col>
                <v-col style="max-width:150px;">
                    <SubscriptionId style="margin-top: 0px; margin-left: 10px;" offline label="구독 ID" v-model="value.parameters.subscriptionId" :editMode="editMode" @change="change"/>
                </v-col>
                <v-col>
                    <v-btn class="gs-query-search-btn contrast-primary-text"
                        @click="search"
                        small
                        style="margin-left: 35px; margin-top: 30px;"
                        color="primary"
                    >
                        <v-icon small>mdi-magnify</v-icon>검색
                    </v-btn>
                </v-col>
            </v-row>
        </v-card-text>
    </v-card>
</template>

<script>
    export default {
        name: 'GetPoint',
        components:{
        },
        props: {},
        data: () => ({
            editMode: true,
            value: {
                apiPath: 'points/search/getPoint',
                parameters: {}
            },
        }),
        created() {
        },
        watch: {
        },
        methods: {
            search() {
                let search = null;
                search = this.value;
                this.$emit('search', search);
            },
            change() {
                this.$emit("update:modelValue", this.value);
            },
        }
    }
</script>
<style>
.attributes-list{
    margin-left: 15px;
}
</style>

