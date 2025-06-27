<template>
    <v-card outlined style="border-radius: 0; margin-top: 10px; padding-bottom: 10px; background-color: #F4F5FA;">
        <v-card-title>
            구독 열람
        </v-card-title>
    
        <v-card-text style="margin-top:-10px;">
            <v-row no-gutters style="margin: 15px 0px -10px -15px;">
                <v-col style="max-width:140px;">
                    <Number class="attributes-list" label="Id" v-model="value.parameters.id" :editMode="editMode"/>
                </v-col>
                <v-col style="max-width:140px;">
                    <Boolean class="attributes-list" label="구독 여부" v-model="value.parameters.isSubscription" :editMode="editMode"/>
                </v-col>
                <v-col style="max-width:140px;">
                    <Date class="attributes-list" label="대여시작기간" v-model="value.parameters.rentalstart" :editMode="editMode"/>
                </v-col>
                <v-col style="max-width:140px;">
                    <Date class="attributes-list" label="대여종료기간" v-model="value.parameters.endSubscription" :editMode="editMode"/>
                </v-col>
                <v-col style="max-width:140px;">
                    <String class="attributes-list" label="도서 URL" v-model="value.parameters.webUrl" :editMode="editMode"/>
                </v-col>
                <v-col style="max-width:140px;">
                    <String class="attributes-list" label="Subscribe" v-model="value.parameters.subscribe" :editMode="editMode"/>
                </v-col>
                <v-col style="max-width:150px;">
                    <BookId style="margin-top: 0px; margin-left: 10px;" offline label="도서 ID" v-model="value.parameters.bookId" :editMode="editMode" @change="change"/>
                </v-col>
                <v-col style="max-width:150px;">
                    <UserId style="margin-top: 0px; margin-left: 10px;" offline label="구독자 ID" v-model="value.parameters.userId" :editMode="editMode" @change="change"/>
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
        name: 'GetSubscription',
        components:{
        },
        props: {},
        data: () => ({
            editMode: true,
            value: {
                apiPath: 'subscriptions/search/getSubscription',
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

